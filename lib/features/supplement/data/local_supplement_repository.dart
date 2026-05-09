import 'dart:async';
import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';
import 'package:supplement_routine/core/models/supplement.dart';
import 'package:supplement_routine/features/supplement/data/supplement_repository.dart';

class LocalSupplementRepository implements SupplementRepository {
  LocalSupplementRepository(
    this._preferences, {
    List<Supplement> seedSupplements = const [],
  }) : _seedSupplements = seedSupplements {
    _supplements = _loadSupplements();
  }

  static const storageKey = 'supplements';

  final SharedPreferencesWithCache _preferences;
  final List<Supplement> _seedSupplements;
  late List<Supplement> _supplements;

  @override
  List<Supplement> getSupplements() {
    return List.unmodifiable(_supplements);
  }

  @override
  List<Supplement> addSupplement(Supplement supplement) {
    _supplements = [..._supplements, supplement];
    _save();
    return getSupplements();
  }

  @override
  List<Supplement> updateSupplement(Supplement updatedSupplement) {
    _supplements = [
      for (final supplement in _supplements)
        if (supplement.id == updatedSupplement.id)
          updatedSupplement
        else
          supplement,
    ];
    _save();
    return getSupplements();
  }

  @override
  List<Supplement> removeSupplement(String supplementId) {
    _supplements = [
      for (final supplement in _supplements)
        if (supplement.id != supplementId) supplement,
    ];
    _save();
    return getSupplements();
  }

  @override
  List<Supplement> clearSupplements() {
    _supplements = [];
    _save();
    return getSupplements();
  }

  List<Supplement> _loadSupplements() {
    final rawJson = _preferences.getString(storageKey);

    if (rawJson == null) {
      return [..._seedSupplements];
    }

    final values = jsonDecode(rawJson) as List;
    return values
        .map(
          (value) =>
              Supplement.fromJson(Map<String, Object?>.from(value as Map)),
        )
        .toList();
  }

  void _save() {
    final encoded = jsonEncode(
      _supplements.map((supplement) => supplement.toJson()).toList(),
    );

    unawaited(_preferences.setString(storageKey, encoded));
  }
}
