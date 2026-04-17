const { createApp } = Vue;

createApp({
    data() {
        return {
            // API 기본 URL
            apiUrl: 'http://localhost:8080/api/v1',
            
            // 인증
            token: localStorage.getItem('token') || '',
            user: JSON.parse(localStorage.getItem('user') || 'null'),
            
            // 로그인 폼
            loginForm: {
                email: '',
                password: ''
            },
            
            // 비밀번호 등록 폼
            credForm: {
                serviceName: '',
                credentialType: 'SERVER',
                url: '',
                username: '',
                password: '',
                port: null,
                description: ''
            },
            
            // 비밀번호 목록
            credentials: [],
            
            // 상태
            errorMsg: '',
            notification: null,
			
			// 회원가입 토글
			isSignup: false,

			// 회원가입 폼
			signupForm: {
			    email: '',
			    password: '',
			    name: ''
			}
        };
    },
    
    mounted() {
        if (this.token) {
            this.setupAxios();
            this.loadCredentials();
        }
    },
    
    methods: {
        // Axios 설정
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
                    this.showNotification('로그인 성공!', 'success');
                }
            } catch (error) {
                this.errorMsg = error.response?.data?.error?.message || '로그인 실패';
            }
        },
        
        // 로그아웃
        logout() {
            this.token = '';
            this.user = null;
            this.credentials = [];
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            delete axios.defaults.headers.common['Authorization'];
        },
        
        // 비밀번호 목록 조회
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
                
                // 마스킹 상태면 먼저 조회
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
        
        // 알림
        showNotification(message, type) {
            this.notification = { message, type };
            setTimeout(() => {
                this.notification = null;
            }, 3000);
        },
		
		// 회원가입
		async signup() {
		    try {
		        this.errorMsg = '';
		        const res = await axios.post(`${this.apiUrl}/auth/signup`, this.signupForm);
		        
		        if (res.data.success) {
		            this.showNotification('회원가입 완료! 로그인해주세요.', 'success');
		            this.isSignup = false;
		            this.signupForm = { email: '', password: '', name: '' };
		        }
		    } catch (error) {
		        this.errorMsg = error.response?.data?.error?.message || '회원가입 실패';
		    }
		}
    }
}).mount('#app');