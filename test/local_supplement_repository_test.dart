import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:shared_preferences_platform_interface/in_memory_shared_preferences_async.dart';
import 'package:shared_preferences_platform_interface/shared_preferences_async_platform_interface.dart';
import 'package:supplement_routine/core/models/intake_method.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/supplement/data/local_supplement_repository.dart';

void main() {
  Supplement createSupplement({String id = 'test_supplement'}) {
    return Supplement(
      id: id,
      name: '테스트 영양제',
      dailyCount: 1,
      method: IntakeMethod.fixedTime,
      dosageUnit: '개',
      dosageValue: 1,
      isNotificationEnabled: true,
    );
  }

  test('저장된 영양제 목록을 로컬 저장소에서 불러온다', () async {
    final supplement = createSupplement();
    final store = InMemorySharedPreferencesAsync.empty();
    SharedPreferencesAsyncPlatform.instance = store;
    await store.setString(
      LocalSupplementRepository.storageKey,
      jsonEncode([supplement.toJson()]),
      const SharedPreferencesOptions(),
    );
    final preferences = await SharedPreferencesWithCache.create(
      cacheOptions: const SharedPreferencesWithCacheOptions(
        allowList: {LocalSupplementRepository.storageKey},
      ),
    );

    final repository = LocalSupplementRepository(preferences);

    expect(repository.getSupplements().single.id, supplement.id);
  });

  test('영양제 변경 사항을 로컬 저장소에 저장한다', () async {
    SharedPreferencesAsyncPlatform.instance =
        InMemorySharedPreferencesAsync.empty();
    final preferences = await SharedPreferencesWithCache.create(
      cacheOptions: const SharedPreferencesWithCacheOptions(
        allowList: {LocalSupplementRepository.storageKey},
      ),
    );
    final repository = LocalSupplementRepository(preferences);

    repository.clearSupplements();
    repository.addSupplement(createSupplement());

    final rawJson = preferences.getString(LocalSupplementRepository.storageKey);
    final values = jsonDecode(rawJson!) as List;

    expect(values, hasLength(1));
    expect(values.first['id'], 'test_supplement');
  });
}
