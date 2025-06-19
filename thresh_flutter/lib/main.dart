import 'package:flutter/material.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'IdeaMemo Flutter Module',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const FlutterIntegrationPage(),
    );
  }
}

class FlutterIntegrationPage extends StatefulWidget {
  const FlutterIntegrationPage({super.key});

  @override
  State<FlutterIntegrationPage> createState() => _FlutterIntegrationPageState();
}

class _FlutterIntegrationPageState extends State<FlutterIntegrationPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('IdeaMemo Flutter 集成'),
        backgroundColor: Colors.blue,
        foregroundColor: Colors.white,
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Icon(
              Icons.integration_instructions,
              size: 80,
              color: Colors.blue,
            ),
            const SizedBox(height: 20),
            const Text(
              'Flutter 模块集成成功!',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 10),
            const Text(
              'Thresh框架准备就绪',
              style: TextStyle(
                fontSize: 16,
                color: Colors.grey,
              ),
            ),
            const SizedBox(height: 30),
            ElevatedButton(
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('Flutter与Android原生通信成功！'),
                    backgroundColor: Colors.green,
                  ),
                );
              },
              child: const Text('测试通信'),
            ),
            const SizedBox(height: 10),
            OutlinedButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: const Text('返回原生应用'),
            ),
          ],
        ),
      ),
    );
  }
}
