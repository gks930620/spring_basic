import 'dart:convert';
import 'package:stomp_dart_client/stomp_dart_client.dart';
import '../models/chat_message.dart';

class StompService {
  StompClient? _client;
  bool get isConnected => _client?.connected ?? false;

  void connect({
    required String url,
    required String roomId,
    required String accessToken,
    required void Function(ChatMessage) onMessage,
    required void Function() onConnect,
    required void Function(dynamic) onError,
  }) {
    _client = StompClient(
      config: StompConfig(
        url: url,
        onConnect: (frame) {
          _client?.subscribe(
            destination: '/sub/room/$roomId',
            callback: (frame) {
              if (frame.body != null) {
                final body = frame.body!;
                try {
                  final decoded = jsonDecode(body);
                  if (decoded is Map<String, dynamic>) {
                    onMessage(ChatMessage.fromJson(decoded));
                    return;
                  } else if (decoded is String) {
                    // 서버가 문자열을 보내는 경우
                    onMessage(ChatMessage(sender: null, content: decoded, type: 'SYSTEM'));
                    return;
                  }
                } catch (e) {
                  // JSON 파싱 실패 -> 서버에서 단순 텍스트로 보낸 입장/퇴장 알림일 가능성
                  final content = body;
                  String type = 'SYSTEM';
                  final lower = content.toLowerCase();
                  if (lower.contains('입장') || lower.contains('들어왔')) type = 'ENTER';
                  if (lower.contains('퇴장') || lower.contains('나갔')) type = 'LEAVE';
                  onMessage(ChatMessage(sender: null, content: content, type: type));
                  return;
                }
              }
            },
          );
          onConnect();
        },
        beforeConnect: () async {},
        onWebSocketError: onError,
        stompConnectHeaders: {
          'Authorization': 'Bearer $accessToken',
          'roomId': roomId,
        },
        webSocketConnectHeaders: {
          'Authorization': 'Bearer $accessToken',
          'roomId': roomId,
        },
      ),
    );
    _client?.activate();
  }

  void sendMessage({
    required String roomId,
    required String content,
  }) {
    _client?.send(
      destination: '/pub/room/$roomId',
      body: jsonEncode({'content': content}),
    );
  }

  void disconnect() {
    _client?.deactivate();
  }
}
