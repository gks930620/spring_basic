class ChatMessage {
  final String? sender; // 서버에서 가끔 null로 올 수 있음
  final String content;
  final String type; // 예: 'CHAT', 'ENTER', 'LEAVE', 'SYSTEM' 등

  ChatMessage({required this.sender, required this.content, this.type = 'CHAT'});

  bool get isSystem => type.toUpperCase() != 'CHAT';

  factory ChatMessage.fromJson(Map<String, dynamic> json) {
    // 다양한 서버 필드 이름을 유연하게 처리
    final rawType = json['type'] ?? json['messageType'] ?? json['msgType'] ?? 'CHAT';
    final typeStr = rawType?.toString() ?? 'CHAT';

    final senderVal = json['sender'] ?? json['senderName'] ?? json['username'];
    final senderStr = senderVal == null ? null : senderVal.toString();

    final contentVal = json['content'] ?? json['message'] ?? json['msg'] ?? '';
    final contentStr = contentVal == null ? '' : contentVal.toString();

    return ChatMessage(sender: senderStr, content: contentStr, type: typeStr);
  }
}
