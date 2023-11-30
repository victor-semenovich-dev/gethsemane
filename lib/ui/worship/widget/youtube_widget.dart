import 'package:flutter/material.dart';

class YoutubeWidget extends StatefulWidget {
  final String? poster;
  final Function() onClick;

  const YoutubeWidget({
    super.key,
    required this.poster,
    required this.onClick,
  });

  @override
  State<YoutubeWidget> createState() => _YoutubeWidgetState();
}

class _YoutubeWidgetState extends State<YoutubeWidget> {
  bool _isPressed = false;

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.black12,
      child: GestureDetector(
        onTapDown: (details) {
          setState(() {
            _isPressed = true;
          });
        },
        onTapUp: (details) {
          setState(() {
            _isPressed = false;
          });
        },
        onTap: () {
          widget.onClick();
        },
        child: Stack(
          alignment: Alignment.center,
          children: [
            AspectRatio(
              aspectRatio: 16.0 / 9.0,
              child: Image.network(
                widget.poster ?? '',
                fit: BoxFit.cover,
                errorBuilder: (context, error, stackTrace) {
                  return Image.asset(
                    'assets/cover_worship_default.jpg',
                    fit: BoxFit.cover,
                  );
                },
              ),
            ),
            Image.asset(
              _isPressed
                  ? 'assets/ic_youtube_red.png'
                  : 'assets/ic_youtube_dark.png',
              width: 60,
            ),
          ],
        ),
      ),
    );
  }
}
