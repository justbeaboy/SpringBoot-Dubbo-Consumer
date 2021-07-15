package com.nacos.test.controller.redisdalaymq.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 仿Optional 实现链式调用bean中value的方法
 *
 * @author Mr.SoftRock
 * @Date 2021/7/14 14:32
 **/
public final class OptionalBean<T> {

    /**
     * Common instance for {@code empty()}.
     */
    private static final OptionalBean<?> EMPTY = new OptionalBean<>();

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final T value;

    private OptionalBean() {
        this.value = null;
    }

    /**
     * 空值会抛出NPE
     *
     * @param value the non-null value to be present
     * @throws NullPointerException if value is null
     */
    private OptionalBean(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * 返回一个不能为空的bean
     *
     * @param <T>   the class of the value
     * @param value the value to be present, which must be non-null
     * @return an {@code Optional} with the value present
     * @throws NullPointerException if value is null
     */
    public static <T> OptionalBean<T> of(T value) {
        return new OptionalBean<>(value);
    }


    /**
     * 包装一个可能为空的bean
     *
     * @param <T>   the class of the value
     * @param value the possibly-null value to describe
     * @return an {@code Optional} with a present value if the specified value
     * is non-null, otherwise an empty {@code Optional}
     */
    public static <T> OptionalBean<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * 取出具体的值，如果为空，就返回  null ,不抛出异常
     *
     * @return
     */
    public T get() {
        return Objects.isNull(value) ? null : value;
    }

    /**
     * 获取一个可能为空的对象
     *
     * @param fn
     * @param <R>
     * @return
     */
    public <R> OptionalBean<R> getBean(Function<? super T, ? extends R> fn) {
        return Objects.isNull(value) ? OptionalBean.empty() : OptionalBean.ofNullable(fn.apply(value));

    }

    /**
     * 如果目标值为空 获取一个默认值
     *
     * @param other
     * @return
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }


    /**
     * 入股目标值为空，通过lambda表达式获取一个值
     *
     * @param other
     * @return
     */
    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }

    /**
     * 返回一个null 值
     *
     * @param <T>
     * @return
     */
    public static <T> OptionalBean<T> empty() {
        @SuppressWarnings("unchecked")
        OptionalBean<T> t = (OptionalBean<T>) EMPTY;
        return t;
    }

    /**
     * 如果目标值为空，就抛出异常
     *
     * @param exceptionSupplier
     * @param <X>
     * @return
     * @throws X
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 判断是否存在
     *
     * @param consumer
     */
    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public boolean ifPresent() {
        return value != null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public static void main(String[] args) {
//        Test test = new Test();
//        test.setName("你好 test");
//
////        Test.Test1 test1 = new Test.Test1();
////        test.setTest1(test1);
//
//        String value1 = OptionalBean.ofNullable(test).getBean(Test::getTest1).getBean(Test.Test1::getName).get();
//        System.out.println("value1-->" + value1);
//        boolean present = OptionalBean.ofNullable(test).getBean(Test::getTest1).getBean(Test.Test1::getName).ifPresent();
//        System.out.println("present-->" + present);
    }
}
