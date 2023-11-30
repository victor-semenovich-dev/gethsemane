import 'package:flutter/material.dart';
import 'package:gethsemane/domain/model/worship_extended.dart';
import 'package:gethsemane/ui/worship/widget/youtube_widget.dart';

class WorshipWidget extends StatelessWidget {
  final WorshipExtended worship;

  const WorshipWidget({super.key, required this.worship});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Container(
        padding: const EdgeInsets.all(8.0),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(8.0),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            YoutubeWidget(
              poster: worship.worshipData.poster,
              onClick: () {},
            ),
          ],
        ),
      ),
    );
  }
}
