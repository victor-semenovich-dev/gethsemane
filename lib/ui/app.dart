import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:gethsemane/ui/repositories.dart';
import 'package:gethsemane/ui/router.dart';

class GethsemaneApp extends StatelessWidget {
  const GethsemaneApp({super.key});

  @override
  Widget build(BuildContext context) {
    return RepositoriesProvider(
      child: MaterialApp.router(
        title: 'Гефсимания',
        routerConfig: router,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(
            seedColor: const Color(0xFFC5564E),
            background: Colors.white.withAlpha(220),
          ),
          useMaterial3: true,
        ),
        localizationsDelegates: AppLocalizations.localizationsDelegates,
        supportedLocales: AppLocalizations.supportedLocales,
      ),
    );
  }
}
