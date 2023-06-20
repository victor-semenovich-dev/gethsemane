import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/ui/worship/worship_cubit.dart';

class WorshipPage extends StatelessWidget {
  const WorshipPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Text(
        context.read<WorshipCubit>().id.toString(),
        style: const TextStyle(fontSize: 40),
      ),
    );
  }
}
