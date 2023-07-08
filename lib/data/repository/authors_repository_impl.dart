import 'package:gethsemane/data/local/database.dart';
import 'package:gethsemane/data/remote/service/api_geth_service.dart';
import 'package:gethsemane/data/util/mappings.dart';
import 'package:gethsemane/domain/repository/authors_repository.dart';

class AuthorsRepositoryImpl extends AuthorsRepository {
  final AppDatabase database;
  final ApiGethService apiGethService;

  AuthorsRepositoryImpl({
    required this.database,
    required this.apiGethService,
  });

  @override
  Future<void> loadAuthors() async {
    final response = await apiGethService.getAuthors();
    if (response.isSuccessful) {
      final authorDtoList = response.body;
      if (authorDtoList != null) {
        await database.batch((batch) {
          batch.deleteAll(database.author);
          batch.insertAllOnConflictUpdate(
            database.author,
            authorDtoList.map((dto) => authorDtoToDbEntity(dto)),
          );
        });
      }
    } else {
      throw response.error ?? 'An error occurred: ${response.statusCode}';
    }
  }
}
