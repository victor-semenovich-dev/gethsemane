import 'package:flutter/material.dart';
import 'package:gethsemane/domain/model/worship_extended.dart';
import 'package:youtube_player_flutter/youtube_player_flutter.dart';

class WorshipWidget extends StatefulWidget {
  final WorshipExtended worship;

  const WorshipWidget({super.key, required this.worship});

  @override
  State<WorshipWidget> createState() => _WorshipWidgetState();
}

class _WorshipWidgetState extends State<WorshipWidget> {
  late YoutubePlayerController? _youtubeController;

  @override
  void initState() {
    super.initState();
    final youtubeVideoId = widget.worship.worshipData.video;
    if (youtubeVideoId != null) {
      _youtubeController = YoutubePlayerController(
        initialVideoId: youtubeVideoId,
        flags: const YoutubePlayerFlags(
          autoPlay: false,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_youtubeController != null) {
      return Wrap(
        direction: Axis.vertical,
        children: [
          YoutubePlayer(
            controller: _youtubeController!,
          ),
        ],
      );
    } else {
      return Container();
    }
  }
}
