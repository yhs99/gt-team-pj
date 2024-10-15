# 멤버
|<img src="https://avatars.githubusercontent.com/u/45925957?v=4" width="130" height="130">| <img src="https://avatars.githubusercontent.com/u/40009468?v=4" width="130" height="130">|<img src="https://avatars.githubusercontent.com/u/169216626?v=4" width="130" height="130">|
|---|---|---|
|팀장 [윤희성](https://github.com/yhs99) | [이현명](https://github.com/HyunmyoungLee) | [최범준](https://github.com/bumjun12)

|<img src="https://avatars.githubusercontent.com/u/169752511?v=4" width="130" height="130">| <img src="https://avatars.githubusercontent.com/u/19566619?v=4" width="130" height="130">|<img src="https://avatars.githubusercontent.com/u/179418413?v=4" width="130" height="130">
|---|---|---|
| [김민성](https://github.com/minsung12345) | [김다손](https://github.com/kimdason) | [이연화](https://github.com/peachea27) 

----------
# nginx 개발환경 구축 방법
  1. 프로젝트 폴더 생성하기(경로는 자유)
![image](https://github.com/user-attachments/assets/3ca3a3f3-76bb-4296-ad9f-c7b42b44826b)
  2. 해당 폴더로 들어가서 해당 경로의 git bash로 열기
![image](https://github.com/user-attachments/assets/9243ca67-4106-488b-ab88-b0c9e15302a4)
  3. git clone하기<br>
     ```1.  git clone https://github.com/yhs99/gt-team-pj.git ``` <br>
     ```2.  npm install ```<br>
     위 명령어 실행
  5. nginx 다운로드<br>
    https://nginx.org/download/nginx-1.27.2.zip
  6. 다운로드 파일을 압축해제 해준 후 안의 내용물을 모두 프로젝트 폴더의 가장 상위폴더로 옮겨준다
![image](https://github.com/user-attachments/assets/c24f5e03-70ed-4e41-90fc-d0f7bac067e3)
  7. conf/nginx.conf 파일을 파일편집기(vscode)로 열어준다<br>
  아래 설정을 추가해준다. (파일 경로는 \ 역슬래시)
```
  location / {
            #root   html;
            root   frontend;
            index  \view\index.html;
  }

  location /api/ {
      proxy_pass http://localhost:8088/;  # 자신의 Spring Tomcat 서버 주소 및 포트
      proxy_set_header Host $host;
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto $scheme;
      
      rewrite ^/api/(.*)$ /$1 break;
  }
```
![image](https://github.com/user-attachments/assets/cf0722c4-b23d-4cc1-a71b-a8c065a6b321)

  8. nginx 실행, 중지, 재시작하기 <br>
     자신의 프로젝트 폴더에서 (nginx.exe가 있는 폴더) 우클릭 -> 터미널에서 열기
     ![image](https://github.com/user-attachments/assets/a978148a-7532-4bb0-a6f2-fb82ff53c0ec)
     <br>
     해당 경로의 cmd창에서
     ```
       ----- nginx 시작
       nginx
       ----- nginx 종료
       nginx -s stop
       ----- nginx 재시작
       nginx -s reload
     ```
     명령어를 사용해 nginx를 제어가 가능합니다

# Backend 기본 설정
1. Tomcat 9 버전 설치 및 설정해주기
2. /src/main/resources/connection-template.properties 복사 후 (삭제해도 무방)
3. connection.properties로 이름 수정 or 붙여넣기
4. connection.properties 내용 db 커넥션 주소에 맞게 수정해주기

![image](https://github.com/user-attachments/assets/5c411e13-0b56-4f95-bbf4-9a33f94fa9b7)

5. tomcat 기본 url 변경(context-path)
![image](https://github.com/user-attachments/assets/5dd07ec9-8aec-4b22-ae80-c4b8f73b9ea6)
기본 url을 /로 설정해주기
port는 마음대로 설정하되, nginx의 location /api/ 의 값과 일치해야합니다
