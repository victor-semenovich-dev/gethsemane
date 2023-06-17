import 'package:flutter/material.dart';
import 'package:logging/logging.dart';

void initializeLogging() {
  Logger.root.level = Level.ALL;
  Logger.root.onRecord.listen((rec) {
    debugPrint('${rec.level.name} - ${rec.time}: ${rec.message}');
  });
}
