# Spring Chat Flutter App

Flutter로 만든 간단한 채팅 앱으로, Spring 백엔드(`spring_chat_backweb`)의 REST + STOMP(WebSocket)와 연동됩니다.

## 기능
- 로그인(/login)
- 내 정보 조회(/api/me)
- 채팅방 목록(/api/rooms)
- 채팅방 입장 후 STOMP 구독/발행(`/sub/room/{roomId}`, `/pub/room/{roomId}`)
- 로그아웃(/api/logout)

## 백엔드 준비
Windows(cmd):

```
cd spring_chat_backweb
gradlew.bat bootRun
```

기본 포트는 8080이며, 시드 사용자 계정은 다음과 같습니다.
- user1 / pass1
- user2 / pass2
- user3 / pass3

## 앱 실행
Windows(cmd):

```
cd spring_chat_app
flutter pub get
flutter run
```

- Android 에뮬레이터에서 `localhost` 대신 호스트 PC에 접속하기 위해 `10.0.2.2`를 사용합니다. 코드에서 자동 처리되어 별도 설정이 필요 없습니다.
- iOS 시뮬레이터/웹 등 다른 타겟은 `localhost:8080` 기준입니다.

## 사용법
1) 앱 실행 → 로그인 화면에서 `user1 / pass1`로 로그인
2) 채팅방 목록에서 방 선택 → 채팅방 입장
3) 웹 브라우저에서 `http://localhost:8080/rooms` 페이지에서 동일 방으로 이동
4) 앱과 웹에서 메시지를 주고받으면 서로 실시간으로 수신됩니다.

## 참고
- STOMP 엔드포인트: `/ws-chat` (SockJS)
- Application Prefix: `/pub`
- Subscribe Prefix: `/sub`
- STOMP CONNECT 시 `Authorization: Bearer {accessToken}` 및 `roomId`를 Native Header로 전송합니다.
- Android `AndroidManifest.xml`에 `android:usesCleartextTraffic="true"` 설정되어 있어 HTTP/ws 사용 가능합니다.

