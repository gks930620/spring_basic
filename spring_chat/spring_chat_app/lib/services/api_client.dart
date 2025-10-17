import 'dart:convert';
import 'package:http/http.dart' as http;
import '../core/config.dart';
import '../models/room.dart';

class ApiClient {
  String? _accessToken;
  String? _refreshToken;
  String? get accessToken => _accessToken;

  Future<bool> login(String username, String password) async {
    try {
      final uri = Uri.parse('${AppConfig.httpBaseUrl}/login');
      final res = await http
          .post(
            uri,
            headers: {'Content-Type': 'application/json'},
            body: jsonEncode({'username': username, 'password': password}),
          )
          .timeout(const Duration(seconds: 10));

      if (res.statusCode == 200) {
        final data = jsonDecode(res.body);
        // 다양한 키 이름 지원
        _accessToken = data['access_token'] ?? data['accessToken'] ?? data['token'];
        _refreshToken = data['refresh_token'] ?? data['refreshToken'] ?? data['refresh'];
        if (_accessToken == null) {
          // ignore: avoid_print
          print('[login] 200 but no token in body: ${res.body}');
          return false;
        }
        return true;
      } else {
        // ignore: avoid_print
        print('[login] HTTP ${res.statusCode}: ${res.body}');
        return false;
      }
    } catch (e) {
      // ignore: avoid_print
      print('[login] error: $e');
      return false;
    }
  }

  Future<List<Room>> fetchRooms() async {
    try {
      final uri = Uri.parse('${AppConfig.httpBaseUrl}/api/rooms');
      final res = await http
          .get(
            uri,
            headers: {
              if (_accessToken != null) 'Authorization': 'Bearer $_accessToken',
            },
          )
          .timeout(const Duration(seconds: 10));

      // 디버그 출력: 헤더와 원시 바이트, 여러 디코딩 결과
      // 개발 시 콘솔에서 확인해 주세요.
      // ignore: avoid_print
      print('[fetchRooms] status: ${res.statusCode}');
      // ignore: avoid_print
      print('[fetchRooms] headers: ${res.headers}');
      // ignore: avoid_print
      print('[fetchRooms] bodyBytes length: ${res.bodyBytes.length}');

      String chooseBestDecoding(List<int> bytes) {
        String tryUtf8() {
          try {
            return utf8.decode(bytes);
          } catch (_) {
            return '';
          }
        }
        String tryLatin1() {
          try {
            return latin1.decode(bytes);
          } catch (_) {
            return '';
          }
        }

        final u = tryUtf8();
        final l = tryLatin1();
        // 한글 포함 여부 검사
        final hangul = RegExp(r'[\uAC00-\uD7A3]');
        final hasHangulUtf8 = u.isNotEmpty && hangul.hasMatch(u);
        final hasHangulLatin1 = l.isNotEmpty && hangul.hasMatch(l);

        // ignore: avoid_print
        print('[fetchRooms] utf8 sample: ${u.length > 200 ? u.substring(0, 200) : u}');
        // ignore: avoid_print
        print('[fetchRooms] latin1 sample: ${l.length > 200 ? l.substring(0, 200) : l}');

        if (hasHangulUtf8) return u;
        if (hasHangulLatin1) return l;
        // fallback: prefer utf8 if not empty
        if (u.isNotEmpty) return u;
        if (l.isNotEmpty) return l;
        return ''; // 둘 다 실패
      }

      // 시도 복구 함수: 이미 디코딩된 문자열이 모지바케인 경우 복구 시도
      String _fixMaybeEncoding(String s) {
        final hangul = RegExp(r'[\uAC00-\uD7A3]');
        if (hangul.hasMatch(s)) return s; // 이미 한글 포함
        try {
          // received string was likely produced by latin1.decode on UTF-8 bytes
          final bytes = latin1.encode(s);
          final decoded = utf8.decode(bytes);
          if (hangul.hasMatch(decoded)) return decoded;
        } catch (_) {}
        try {
          // reverse: received string was utf8 decoded but should be latin1
          final bytes2 = utf8.encode(s);
          final decoded2 = latin1.decode(bytes2);
          if (hangul.hasMatch(decoded2)) return decoded2;
        } catch (_) {}
        return s; // 복구 실패
      }

      if (res.statusCode == 200) {
        final bodyStr = chooseBestDecoding(res.bodyBytes);
        if (bodyStr.isEmpty) throw Exception('응답 디코딩 실패');
        final list = jsonDecode(bodyStr) as List;
        final rooms = list.map((e) => Room.fromJson(e as Map<String, dynamic>)).toList();
        // 방 이름이 깨졌다면 복구 시도
        final fixed = rooms.map((r) {
          final fixedName = _fixMaybeEncoding(r.name);
          if (fixedName != r.name) return Room(id: r.id, name: fixedName);
          return r;
        }).toList();
        return fixed;
      }
      // ignore: avoid_print
      print('[fetchRooms] HTTP ${res.statusCode}: ${res.body}');
      throw Exception('방 목록 조회 실패');
    } catch (e) {
      // ignore: avoid_print
      print('[fetchRooms] error: $e');
      rethrow;
    }
  }

  // 디버그용: 원시 응답과 utf8/latin1 샘플을 돌려줍니다.
  Future<Map<String, dynamic>> fetchRoomsRaw() async {
    final uri = Uri.parse('${AppConfig.httpBaseUrl}/api/rooms');
    final res = await http.get(
      uri,
      headers: {if (_accessToken != null) 'Authorization': 'Bearer $_accessToken'},
    ).timeout(const Duration(seconds: 10));

    final headers = Map<String, String>.from(res.headers);
    String utf8Sample = '';
    String latin1Sample = '';
    try {
      utf8Sample = utf8.decode(res.bodyBytes);
    } catch (_) {}
    try {
      latin1Sample = latin1.decode(res.bodyBytes);
    } catch (_) {}

    return {
      'status': res.statusCode,
      'headers': headers,
      'utf8Sample': utf8Sample,
      'latin1Sample': latin1Sample,
      'bodyBytesLength': res.bodyBytes.length,
      'bodyRaw': res.body,
    };
  }

  // 새 메서드: /api/me 를 호출해 username을 가져옵니다.
  Future<String?> getUsername() async {
    if (_accessToken == null) return null;
    try {
      final uri = Uri.parse('${AppConfig.httpBaseUrl}/api/me');
      final res = await http.get(
        uri,
        headers: {'Authorization': 'Bearer $_accessToken'},
      ).timeout(const Duration(seconds: 10));
      if (res.statusCode == 200) {
        final data = jsonDecode(res.body);
        return data['username'] ?? data['userName'] ?? data['name'] ?? data['username'];
      }
      return null;
    } catch (e) {
      // ignore: avoid_print
      print('[getUsername] error: $e');
      return null;
    }
  }

  // 새 메서드: 로그아웃 - 서버에 리프레시 토큰으로 요청 시도 후 로컬 토큰 제거
  Future<void> logout() async {
    final rt = _refreshToken;
    // 먼저 로컬 토큰 제거
    _accessToken = null;
    _refreshToken = null;
    try {
      if (rt != null) {
        final uri = Uri.parse('${AppConfig.httpBaseUrl}/api/logout');
        await http.post(uri, headers: {'RefreshToken': rt}).timeout(const Duration(seconds: 5));
      }
    } catch (e) {
      // ignore: avoid_print
      print('[logout] error (ignored): $e');
    }
  }
}
