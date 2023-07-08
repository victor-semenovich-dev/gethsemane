import 'package:flutter/material.dart';
import 'package:gethsemane/data/local/database.dart';
import 'package:gethsemane/data/remote/service/api_geth_mobile_service.dart';
import 'package:gethsemane/domain/repository/worships_repository.dart';

class WorshipsRepositoryImpl extends WorshipsRepository {
  final AppDatabase database;
  final ApiGethMobileService apiGethMobileService;

  WorshipsRepositoryImpl({
    required this.database,
    required this.apiGethMobileService,
  });

  @override
  Future<void> getWorship(int id) async {
    final response = await apiGethMobileService.getWorship(id);
    if (response.isSuccessful) {
      final worshipDto = response.body;
      if (worshipDto != null) {
        debugPrint('dto - $worshipDto');
      }
    } else {
      throw response.error ?? 'An error occurred: ${response.statusCode}';
    }
  }
}
