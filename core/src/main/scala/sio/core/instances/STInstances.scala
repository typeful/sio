package sio.core.instances

import cats.MonadError
import sio.core.ST

trait STInstances {
  implicit def stMonadError[S]: MonadError[ST[S, ?], Throwable] =
    new MonadError[ST[S, ?], Throwable] {
      override def pure[A](x: A): ST[S, A] =
        ST.pure(x)
      override def raiseError[A](e: Throwable): ST[S, A] =
        ST.raise(e)

      override def map[A, B](fa: ST[S, A])(f: A => B): ST[S, B] =
        fa.map(f)
      override def flatMap[A, B](fa: ST[S, A])(f: A => ST[S, B]): ST[S, B] =
        fa.flatMap(f)
      override def handleErrorWith[A](fa: ST[S, A])(f: Throwable => ST[S, A]): ST[S, A] =
        fa.handleErrorWith(f)

      override def tailRecM[A, B](a: A)(f: A => ST[S, Either[A, B]]): ST[S, B] =
        pure(a).flatMap(a => f(a).flatMap {
          case Right(b) => pure(b)
          case Left(x) => tailRecM(x)(f)
        })
    }
}
