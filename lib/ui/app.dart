import 'package:flutter/material.dart';
import 'package:gethsemane/ui/router.dart';

class GethsemaneApp extends StatelessWidget {
  const GethsemaneApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'Гефсимания',
      routerConfig: router,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFFC5564E)),
        useMaterial3: true,
      ),
    );
  }
}
