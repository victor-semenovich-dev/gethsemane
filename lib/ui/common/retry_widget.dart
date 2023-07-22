import 'package:flutter/material.dart';
import 'package:gethsemane/domain/extensions/buildcontext.dart';

class RetryWidget extends StatelessWidget {
  final Function() onRetryClick;

  const RetryWidget({super.key, required this.onRetryClick});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Wrap(
        direction: Axis.vertical,
        alignment: WrapAlignment.center,
        crossAxisAlignment: WrapCrossAlignment.center,
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Text(context.l10n.errorCommon, textAlign: TextAlign.center),
          ),
          FilledButton(
            onPressed: onRetryClick,
            child: Text(context.l10n.buttonRetry),
          ),
        ],
      ),
    );
  }
}
