import 'package:flutter/material.dart';
import 'package:gethsemane/domain/extensions/buildcontext.dart';
import 'package:intl/intl.dart';

extension DateTimeExt on DateTime {
  String eventTitle(BuildContext context) {
    final date = DateFormat('dd MMMM yyyy', 'ru').format(this);
    final weekday = DateFormat('EE', 'ru').format(this).toUpperCase();
    if (this.weekday == DateTime.sunday) {
      if (hour <= 12) {
        return context.l10n
            .worshipTitleLong(date, context.l10n.partOfDayMorning, weekday);
      } else {
        return context.l10n
            .worshipTitleLong(date, context.l10n.partOfDayEvening, weekday);
      }
    } else {
      return context.l10n.worshipTitleShort(date, weekday);
    }
  }
}
