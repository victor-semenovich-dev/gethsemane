import 'package:json_annotation/json_annotation.dart';

part '../../../generated/data/remote/model/author_dto.g.dart';

@JsonSerializable(fieldRename: FieldRename.snake, createToJson: false)
class AuthorDTO {
  final int id;
  final String name;
  final String? biography;
  final int? sermonsCount;

  AuthorDTO({
    required this.id,
    required this.name,
    required this.biography,
    required this.sermonsCount,
  });

  static const fromJsonFactory = _$AuthorDTOFromJson;

  @override
  String toString() {
    return 'AuthorDTO{id: $id, name: $name, biography: $biography, sermonsCount: $sermonsCount}';
  }
}
