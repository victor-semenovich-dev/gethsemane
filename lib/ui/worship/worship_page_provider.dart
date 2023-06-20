import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/ui/worship/worship_cubit.dart';
import 'package:gethsemane/ui/worship/worship_page.dart';

class WorshipPageProvider extends StatelessWidget {
  final int id;

  const WorshipPageProvider({super.key, required this.id});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => WorshipCubit(id: id),
      child: const WorshipPage(),
    );
  }
}
