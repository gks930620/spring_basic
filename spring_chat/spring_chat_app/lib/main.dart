import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'pages/login_page.dart';
import 'services/api_client.dart';
import 'core/config.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  // .env 로드 (없으면 무시)
  try {
    await dotenv.load(fileName: '.env');
  } catch (_) {
    // ignore
  }
  // 현재 설정된 서버 주소 로그 출력 (문제 디버깅용)
  // ignore: avoid_print
  print('[AppConfig] httpBaseUrl=${AppConfig.httpBaseUrl}, wsUrl=${AppConfig.wsUrl}');

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    final apiClient = ApiClient();
    return MaterialApp(
      title: 'Spring Chat',
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
      ),
      home: LoginPage(apiClient: apiClient),
    );
  }
}
