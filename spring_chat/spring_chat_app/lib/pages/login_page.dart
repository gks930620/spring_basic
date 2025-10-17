import 'package:flutter/material.dart';
import '../services/api_client.dart';
import 'rooms_page.dart';

class LoginPage extends StatefulWidget {
  final ApiClient apiClient;
  const LoginPage({required this.apiClient, super.key});
  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _idCtrl = TextEditingController();
  final _pwCtrl = TextEditingController();
  String? _error;
  bool _loading = false;

  Future<void> _onLogin() async {
    if (_loading) return;
    setState(() {
      _loading = true;
      _error = null;
    });
    final ok = await widget.apiClient.login(_idCtrl.text.trim(), _pwCtrl.text);
    if (!mounted) return;
    setState(() => _loading = false);

    if (ok) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => RoomsPage(apiClient: widget.apiClient)),
      );
    } else {
      setState(() => _error = '로그인 실패. 서버 주소/계정 확인');
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('로그인 실패')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('로그인')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            TextField(controller: _idCtrl, decoration: const InputDecoration(labelText: 'ID')),
            const SizedBox(height: 12),
            TextField(controller: _pwCtrl, decoration: const InputDecoration(labelText: 'PW'), obscureText: true),
            const SizedBox(height: 12),
            if (_error != null) Text(_error!, style: const TextStyle(color: Colors.red)),
            const SizedBox(height: 12),
            ElevatedButton.icon(
              onPressed: _loading ? null : _onLogin,
              icon: _loading
                  ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth: 2))
                  : const Icon(Icons.login),
              label: Text(_loading ? '로그인 중...' : '로그인'),
            ),
          ],
        ),
      ),
    );
  }
}
