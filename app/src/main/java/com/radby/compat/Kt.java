package com.radby.compat;

/**
 * Bridge para o padrao `kotlin.Result` que o jadx materializou com nomes
 * sinteticos (m663constructorimpl / m669isFailureimpl / m670isSuccessimpl).
 * Mantem a semantica boxed: box() e identidade, failure() cria Result.Failure,
 * isFailure() testa a instancia e isSuccess() e o complemento.
 */
public final class Kt {
    private Kt() {
    }

    public static Object box(Object value) {
        return value;
    }

    public static Object failure(Throwable throwable) {
        return new kotlin.Result.Failure(throwable);
    }

    public static boolean isFailure(Object value) {
        return value instanceof kotlin.Result.Failure;
    }

    public static boolean isSuccess(Object value) {
        return !(value instanceof kotlin.Result.Failure);
    }

    public static Throwable exceptionOrNull(Object value) {
        return value instanceof kotlin.Result.Failure ? ((kotlin.Result.Failure) value).exception : null;
    }

    public static void throwOnFailure(Object value) {
        if (value instanceof kotlin.Result.Failure) {
            Kt.<RuntimeException>sneakyThrow(((kotlin.Result.Failure) value).exception);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow(Throwable throwable) throws T {
        throw (T) throwable;
    }
}
