import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gethsemane/ui/common/retry_widget.dart';
import 'package:gethsemane/ui/worship/widget/worship_widget.dart';
import 'package:gethsemane/ui/worship/worship_cubit.dart';
import 'package:gethsemane/ui/worship/worships_state.dart';

class WorshipPage extends StatelessWidget {
  const WorshipPage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<WorshipCubit, WorshipState>(builder: (context, state) {
      final worship = state.worship;
      if (state.isError && worship == null) {
        return RetryWidget(
          onRetryClick: () => context.read<WorshipCubit>().loadWorship(),
        );
      } else if (state.isInProgress && worship == null) {
        return const Center(child: CircularProgressIndicator());
      } else if (worship != null) {
        return Padding(
          padding: const EdgeInsets.all(8.0),
          child: WorshipWidget(worship: worship),
        );
      } else {
        return Container();
      }
    });
  }
}
