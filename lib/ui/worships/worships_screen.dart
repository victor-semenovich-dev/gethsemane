import 'package:flutter/material.dart';

class WorshipsScreen extends StatelessWidget {
  const WorshipsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return Scaffold(
      appBar: AppBar(
        backgroundColor: colorScheme.primary,
        title: Text(
          'Богослужения',
          style: TextStyle(color: colorScheme.onPrimary),
        ),
      ),
      body: const Center(
        child: Text('Здесь будут Богослужения'),
      ),
    );
  }
}
