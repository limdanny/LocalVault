const { createApp } = Vue;

createApp({
    data() {
        return {
            apiUrl: 'http://localhost:8080/api/v1',
            
            // 인증
            token: localStorage.getItem('token') || '',
            user: JSON.parse(localStorage.getItem('user') || 'null'),
            
            // 페이지
            currentPage: 'list',
            
            // 로그인/회원가입
            isSignup: false,
            loginForm: { email: '', password: '' },
            signupForm: { email: '', password: '', name: '' },
            errorMsg: '',
            
            // 비밀번호
            credentials: [],
            credForm: {
                serviceName: '',
                credentialType: 'SERVER',
                url: '',
                username: '',
                password: '',
                port: null,
                description: ''
            },
            
            // 사용자 (관리자용)
            members: [],
            
            // 권한 부여
            permForm: { memberId: '', credentialId: '' },
            
            // 알림
            notification: null
        };
    },
    
    mounted() {
        if (this.token) {
            this.setupAxios();
            this.loadCredentials();
            if (this.user?.role === 'ADMIN') {
                this.loadMembers();
            }
        }
    },
    
    methods: {
        setupAxios() {
            axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`;
        },
        
        // 로그인
        async login() {
            try {
                this.errorMsg = '';
                const res = await axios.post(`${this.apiUrl}/auth/login`, this.loginForm);
                if (res.data.success) {
                    this.token = res.data.data.accessToken;
                    this.user = res.data.data.member;
                    localStorage.setItem('token', this.token);
                    localStorage.setItem('user', JSON.stringify(this.user));
                    this.setupAxios();
                    this.loadCredentials();
                    if (this.user.role === 'ADMIN') {
                        this.loadMembers();
                    }
                    this.showNotification('로그인 성공!', 'success');
                }
            } catch (error) {
                this.errorMsg = error.response?.data?.error?.message || '로그인 실패';
            }
        },
        
        // 회원가입
        async signup() {
            try {
                this.errorMsg = '';
                const res = await axios.post(`${this.apiUrl}/auth/signup`, this.signupForm);
                if (res.data.success) {
                    this.showNotification('회원가입 완료! 관리자 승인 후 로그인 가능합니다.', 'success');
                    this.isSignup = false;
                    this.signupForm = { email: '', password: '', name: '' };
                }
            } catch (error) {
                this.errorMsg = error.response?.data?.error?.message || '회원가입 실패';
            }
        },
        
        // 로그아웃
        logout() {
            this.token = '';
            this.user = null;
            this.credentials = [];
            this.members = [];
            this.currentPage = 'list';
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            delete axios.defaults.headers.common['Authorization'];
        },
        
        // 비밀번호 목록
        async loadCredentials() {
            try {
                const res = await axios.get(`${this.apiUrl}/credentials`, {
                    params: { page: 0, size: 100 }
                });
                if (res.data.success) {
                    this.credentials = res.data.data.content.map(c => ({
                        ...c,
                        revealed: false
                    }));
                }
            } catch (error) {
                console.error('목록 조회 실패:', error);
            }
        },
        
        // 비밀번호 등록
        async createCredential() {
            if (!this.credForm.serviceName || !this.credForm.username || !this.credForm.password) {
                this.showNotification('필수 항목을 입력하세요', 'error');
                return;
            }
            try {
                const res = await axios.post(`${this.apiUrl}/credentials`, this.credForm);
                if (res.data.success) {
                    this.showNotification('등록 완료!', 'success');
                    this.credForm = {
                        serviceName: '',
                        credentialType: 'SERVER',
                        url: '',
                        username: '',
                        password: '',
                        port: null,
                        description: ''
                    };
                    this.loadCredentials();
                    this.currentPage = 'list';
                }
            } catch (error) {
                this.showNotification('등록 실패', 'error');
            }
        },
        
        // 비밀번호 보기/숨기기
        async toggleReveal(cred) {
            if (cred.revealed) {
                cred.revealed = false;
                cred.password = '********';
            } else {
                try {
                    const res = await axios.get(`${this.apiUrl}/credentials/${cred.id}/reveal`);
                    if (res.data.success) {
                        cred.password = res.data.data.password;
                        cred.revealed = true;
                    }
                } catch (error) {
                    this.showNotification('조회 권한이 없습니다', 'error');
                }
            }
        },
        
        // 비밀번호 복사
        async copyPassword(cred) {
            try {
                let password = cred.password;
                if (!cred.revealed) {
                    const res = await axios.get(`${this.apiUrl}/credentials/${cred.id}/reveal`);
                    if (res.data.success) {
                        password = res.data.data.password;
                    }
                }
                await navigator.clipboard.writeText(password);
                this.showNotification('복사 완료!', 'success');
            } catch (error) {
                this.showNotification('복사 실패', 'error');
            }
        },
        
        // 비밀번호 삭제
        async deleteCredential(id) {
            if (!confirm('정말 삭제하시겠습니까?')) return;
            try {
                const res = await axios.delete(`${this.apiUrl}/credentials/${id}`);
                if (res.data.success) {
                    this.showNotification('삭제 완료!', 'success');
                    this.loadCredentials();
                }
            } catch (error) {
                this.showNotification('삭제 실패', 'error');
            }
        },
        
        // 사용자 목록 (관리자)
        async loadMembers() {
            try {
                const res = await axios.get(`${this.apiUrl}/members`);
                if (res.data.success) {
                    this.members = res.data.data;
                }
            } catch (error) {
                console.error('사용자 목록 조회 실패:', error);
            }
        },
        
        // 사용자 승인
        async activateMember(id) {
            try {
                await axios.patch(`${this.apiUrl}/members/${id}/activate`);
                this.showNotification('승인 완료!', 'success');
                this.loadMembers();
            } catch (error) {
                this.showNotification('실패', 'error');
            }
        },
        
        // 사용자 비활성화
        async deactivateMember(id) {
            if (!confirm('정말 비활성화하시겠습니까?')) return;
            try {
                await axios.patch(`${this.apiUrl}/members/${id}/deactivate`);
                this.showNotification('비활성화 완료!', 'success');
                this.loadMembers();
            } catch (error) {
                this.showNotification('실패', 'error');
            }
        },
        
        // 권한 부여
        async grantPermission() {
            if (!this.permForm.memberId || !this.permForm.credentialId) {
                this.showNotification('사용자와 비밀번호를 선택하세요', 'error');
                return;
            }
            try {
                await axios.post(`${this.apiUrl}/permissions`, {
                    memberId: this.permForm.memberId,
                    credentialIds: [this.permForm.credentialId]
                });
                this.showNotification('권한 부여 완료!', 'success');
                this.permForm = { memberId: '', credentialId: '' };
            } catch (error) {
                this.showNotification('권한 부여 실패', 'error');
            }
        },
        
        // 날짜 포맷
        formatDate(dateStr) {
            if (!dateStr) return '-';
            const date = new Date(dateStr);
            return date.toLocaleDateString('ko-KR');
        },
        
        // 알림
        showNotification(message, type) {
            this.notification = { message, type };
            setTimeout(() => { this.notification = null; }, 3000);
        }
    }
}).mount('#app');