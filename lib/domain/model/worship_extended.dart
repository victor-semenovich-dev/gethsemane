import 'package:gethsemane/data/local/database.dart';

class WorshipExtended {
  final WorshipData worshipData;
  final List<SermonData> sermonDataList;
  final List<SongData> songDataList;
  final List<PhotoData> photoDataList;

  WorshipExtended({
    required this.worshipData,
    required this.sermonDataList,
    required this.songDataList,
    required this.photoDataList,
  });
}
