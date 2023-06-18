import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/domain/repository/events.dart';
import 'package:gethsemane/ui/worships/worships_cubit.dart';
import 'package:gethsemane/ui/worships/worships_screen.dart';

class WorshipsRoute extends StatelessWidget {
  const WorshipsRoute({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => WorshipsCubit(
        eventsRepository: context.read<EventsRepository>(),
      ),
      child: const WorshipsScreen(),
    );
  }
}
