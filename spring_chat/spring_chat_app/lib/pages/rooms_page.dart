import 'package:flutter/material.dart';
import '../services/api_client.dart';
import '../models/room.dart';
import 'chat_room_page.dart';
import 'login_page.dart';

class RoomsPage extends StatelessWidget {
  final ApiClient apiClient;
  const RoomsPage({required this.apiClient, super.key});

  Future<void> _confirmLogout(BuildContext context) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (c) => AlertDialog(
        title: const Text('로그아웃'),
        content: const Text('로그아웃 하시겠습니까?'),
        actions: [
          TextButton(onPressed: () => Navigator.of(c).pop(false), child: const Text('취소')),
          TextButton(onPressed: () => Navigator.of(c).pop(true), child: const Text('로그아웃')),
        ],
      ),
    );

    if (ok != true) return;

    await apiClient.logout();
    if (!context.mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('로그아웃되었습니다.')));
    Navigator.pushReplacement(
      context,
      MaterialPageRoute(builder: (_) => LoginPage(apiClient: apiClient)),
    );
  }

  Future<void> _showRawResponse(BuildContext context) async {
    try {
      final data = await apiClient.fetchRoomsRaw();
      if (!context.mounted) return;
      showDialog<void>(
        context: context,
        builder: (c) => AlertDialog(
          title: const Text('응답 디버그'),
          content: SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('status: ${data['status']}'),
                const SizedBox(height: 8),
                Text('headers:'),
                Text('${data['headers']}', style: const TextStyle(fontSize: 12)),
                const SizedBox(height: 8),
                Text('utf8 sample:'),
                Text('${(data['utf8Sample'] as String).length > 500 ? (data['utf8Sample'] as String).substring(0,500) + "..." : data['utf8Sample']}', style: const TextStyle(fontSize: 12)),
                const SizedBox(height: 8),
                Text('latin1 sample:'),
                Text('${(data['latin1Sample'] as String).length > 500 ? (data['latin1Sample'] as String).substring(0,500) + "..." : data['latin1Sample']}', style: const TextStyle(fontSize: 12)),
                const SizedBox(height: 8),
                Text('bodyBytes length: ${data['bodyBytesLength']}'),
              ],
            ),
          ),
          actions: [TextButton(onPressed: () => Navigator.of(c).pop(), child: const Text('닫기'))],
        ),
      );
    } catch (e) {
      if (!context.mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('디버그 응답 조회 실패: $e')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('채팅방 목록'),
        actions: [
          IconButton(
            tooltip: '응답 디버그',
            icon: const Icon(Icons.bug_report),
            onPressed: () => _showRawResponse(context),
          ),
          IconButton(
            tooltip: '로그아웃',
            icon: const Icon(Icons.logout),
            onPressed: () => _confirmLogout(context),
          ),
        ],
      ),
      body: FutureBuilder<List<Room>>(
        future: apiClient.fetchRooms(),
        builder: (context, snapshot) {
          if (!snapshot.hasData) return const Center(child: CircularProgressIndicator());
          final rooms = snapshot.data!;
          return ListView.builder(
            itemCount: rooms.length,
            itemBuilder: (context, idx) => ListTile(
              title: Text(rooms[idx].name),
              onTap: () {
                Navigator.push(context, MaterialPageRoute(
                  builder: (_) => ChatRoomPage(
                    apiClient: apiClient,
                    room: rooms[idx],
                  ),
                ));
              },
            ),
          );
        },
      ),
    );
  }
}
