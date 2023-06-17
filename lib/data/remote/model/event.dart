class EventDTO {
  final int id;
  final int categoryId;
  final String title;
  final String date;
  final String? note;
  final String? audio;
  final String? shortDesc;
  final int isDraft;
  final int isArchive;
  final int? musicGroupId;
  final String? video;

  EventDTO({
    required this.id,
    required this.categoryId,
    required this.title,
    required this.date,
    required this.note,
    required this.audio,
    required this.shortDesc,
    required this.isDraft,
    required this.isArchive,
    required this.musicGroupId,
    required this.video,
  });

  factory EventDTO.fromJson(Map<String, dynamic> json) => EventDTO(
        id: json['id'],
        categoryId: json['category_id'],
        title: json['title'],
        date: json['date'],
        note: json['note'],
        audio: json['audio'],
        shortDesc: json['short_desc'],
        isDraft: json['is_draft'],
        isArchive: json['is_archive'],
        musicGroupId: json['music_group_id'],
        video: json['video'],
      );

  @override
  String toString() {
    return 'EventDTO{id: $id, categoryId: $categoryId, title: $title, date: $date, note: $note, audio: $audio, shortDesc: $shortDesc, isDraft: $isDraft, isArchive: $isArchive, musicGroupId: $musicGroupId, video: $video}';
  }
}
