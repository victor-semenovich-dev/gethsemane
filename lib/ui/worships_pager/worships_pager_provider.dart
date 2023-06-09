import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_cubit.dart';
import 'package:gethsemane/ui/worships_pager/worships_pager_route.dart';

class WorshipsPagerProvider extends StatelessWidget {
  const WorshipsPagerProvider({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => WorshipsPagerCubit(
        loadInitialDataUseCase: context.read(),
        eventsRepository: context.read(),
      ),
      child: const WorshipsPagerRoute(),
    );
  }
}
