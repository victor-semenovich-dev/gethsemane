import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/domain/repository/events.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_cubit.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_screen.dart';

class WorshipsPagerRoute extends StatelessWidget {
  const WorshipsPagerRoute({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => WorshipsPagerCubit(
        eventsRepository: context.read<EventsRepository>(),
      ),
      child: const WorshipsPagerScreen(),
    );
  }
}
