import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class WorshipsScreen extends StatelessWidget {
  const WorshipsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return Scaffold(
      appBar: AppBar(
        backgroundColor: colorScheme.primary,
        title: Text(
          AppLocalizations.of(context)!.worshipsTitle,
          style: TextStyle(color: colorScheme.onPrimary),
        ),
      ),
      body: Center(
        child: Text(AppLocalizations.of(context)!.worshipsDescription),
      ),
    );
  }
}
