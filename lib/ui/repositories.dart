import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/data/local/database.dart';
import 'package:gethsemane/data/remote/client.dart';
import 'package:gethsemane/data/repository/authors_repository_impl.dart';
import 'package:gethsemane/data/repository/events_repository_impl.dart';
import 'package:gethsemane/data/repository/music_groups_repository_impl.dart';
import 'package:gethsemane/domain/repository/authors_repository.dart';
import 'package:gethsemane/domain/repository/events_repository.dart';
import 'package:gethsemane/domain/repository/music_groups_repository.dart';
import 'package:gethsemane/domain/usecase/load_initial_data_usecase.dart';

class RepositoriesProvider extends StatelessWidget {
  final Widget child;

  const RepositoriesProvider({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return MultiRepositoryProvider(
      providers: [
        RepositoryProvider<AppDatabase>(
          create: (context) => AppDatabase(),
        ),
        RepositoryProvider<HttpClients>(
          create: (context) => HttpClients(),
        ),
        RepositoryProvider<AuthorsRepository>(
          create: (context) => AuthorsRepositoryImpl(
            database: context.read(),
            apiGethService: context.read<HttpClients>().geth.getService(),
          ),
        ),
        RepositoryProvider<MusicGroupsRepository>(
          create: (context) => MusicGroupsRepositoryImpl(
            database: context.read(),
            apiGethsemaneService:
                context.read<HttpClients>().gethsemane.getService(),
          ),
        ),
        RepositoryProvider<LoadInitialDataUseCase>(
          create: (context) => LoadInitialDataUseCase(
            authorsRepository: context.read(),
            musicGroupsRepository: context.read(),
          ),
        ),
        RepositoryProvider<EventsRepository>(
          create: (context) => EventsRepositoryImpl(
            database: context.read(),
            apiGethService: context.read<HttpClients>().geth.getService(),
          ),
        ),
      ],
      child: child,
    );
  }
}
