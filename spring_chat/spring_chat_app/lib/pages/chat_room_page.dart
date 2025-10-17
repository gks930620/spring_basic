import 'package:flutter/material.dart';
import '../models/room.dart';
import '../models/chat_message.dart';
import '../services/api_client.dart';
import '../services/stomp_service.dart';
import '../core/config.dart';

class ChatRoomPage extends StatefulWidget {
  final ApiClient apiClient;
  final Room room;
  const ChatRoomPage({required this.apiClient, required this.room, super.key});
  @override
  State<ChatRoomPage> createState() => _ChatRoomPageState();
}

class _ChatRoomPageState extends State<ChatRoomPage> {
  final StompService _stompService = StompService();
  final List<ChatMessage> _messages = [];
  final _controller = TextEditingController();
  final ScrollController _scrollCtrl = ScrollController();
  bool _connected = false;
  String? _myUsername;

  @override
  void initState() {
    super.initState();
    // 현재 사용자명 조회
    widget.apiClient.getUsername().then((name) {
      setState(() => _myUsername = name);
    });

    _stompService.connect(
      url: AppConfig.wsUrl,
      roomId: widget.room.id.toString(),
      accessToken: widget.apiClient.accessToken!,
      onMessage: (msg) {
        setState(() {
          _messages.add(msg);
        });
        // 새 메시지 오면 맨 아래로
        Future.delayed(const Duration(milliseconds: 100), () {
          if (_scrollCtrl.hasClients) {
            _scrollCtrl.animateTo(
              _scrollCtrl.position.maxScrollExtent,
              duration: const Duration(milliseconds: 200),
              curve: Curves.easeOut,
            );
          }
        });
      },
      onConnect: () => setState(() => _connected = true),
      onError: (err) => setState(() => _connected = false),
    );
  }

  @override
  void dispose() {
    _stompService.disconnect();
    _controller.dispose();
    _scrollCtrl.dispose();
    super.dispose();
  }

  void _send() {
    if (_controller.text.trim().isEmpty) return;
    _stompService.sendMessage(
      roomId: widget.room.id.toString(),
      content: _controller.text.trim(),
    );
    _controller.clear();
  }

  Widget _buildMessageTile(ChatMessage msg) {
    if (msg.isSystem) {
      // 시스템 메시지 (입장/퇴장 등) 가운데 정렬
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 6.0),
        child: Center(
          child: Text(
            msg.content,
            style: TextStyle(color: Colors.grey[600], fontStyle: FontStyle.italic),
          ),
        ),
      );
    }

    final isMine = _myUsername != null && msg.sender == _myUsername;
    final align = isMine ? CrossAxisAlignment.end : CrossAxisAlignment.start;
    final bg = isMine ? Colors.blue[200] : Colors.grey[200];
    final textAlign = isMine ? TextAlign.right : TextAlign.left;

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0, horizontal: 8.0),
      child: Column(
        crossAxisAlignment: align,
        children: [
          if (!isMine)
            Padding(
              padding: const EdgeInsets.only(bottom: 4.0),
              child: Text(msg.sender ?? '알수없음', style: const TextStyle(fontSize: 12, color: Colors.black54)),
            ),
          Container(
            constraints: const BoxConstraints(maxWidth: 400),
            padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 12),
            decoration: BoxDecoration(color: bg, borderRadius: BorderRadius.circular(8)),
            child: Text(msg.content, textAlign: textAlign),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('${widget.room.name}')),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              controller: _scrollCtrl,
              itemCount: _messages.length,
              itemBuilder: (c, i) => _buildMessageTile(_messages[i]),
            ),
          ),
          if (!_connected) const Text('서버 연결 중...'),
          Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _controller,
                  onSubmitted: (_) => _send(),
                  decoration: const InputDecoration(hintText: '메시지 입력'),
                ),
              ),
              IconButton(icon: const Icon(Icons.send), onPressed: _send),
            ],
          ),
        ],
      ),
    );
  }
}
