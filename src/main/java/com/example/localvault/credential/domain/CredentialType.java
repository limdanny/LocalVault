package com.example.localvault.credential.domain;

public enum CredentialType {
    SERVER,     // SSH, SFTP, 원격접속
    DATABASE,   // DB 접속
    SITE,       // 웹사이트
    API,        // API Key
    OTHER       // 기타
}