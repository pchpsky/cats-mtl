package cats
package mtl
package instances

import cats.data.{ReaderT, StateT}

trait AskInstances extends AskInstancesLowPriority {

  implicit def askInd[M[_], Inner[_], E](implicit
                                         lift: MonadLayer.Aux[M, Inner],
                                         under: Ask[Inner, E]
                                        ): Ask[M, E] =
    new Ask[M, E] {
      val monad = lift.monad

      def ask: M[E] =
        lift.layer(under.ask)
    }

}

trait AskInstancesLowPriority {

  implicit def askReader[M[_], E](implicit M: Monad[M]): Ask[CurryT[ReaderTCE[E]#l, M]#l, E] =
    new Ask[CurryT[ReaderTCE[E]#l, M]#l, E] {

      val monad =
        ReaderT.catsDataMonadReaderForKleisli(M)

      def ask: ReaderT[M, E, E] =
        ReaderT.ask[M, E]
    }

  implicit def askState[M[_], S](implicit M: Monad[M]): Ask[CurryT[StateTCS[S]#l, M]#l, S] =
    new Ask[CurryT[StateTCS[S]#l, M]#l, S] {
      val monad =
        StateT.catsDataMonadForStateT(M)

      def ask: StateT[M, S, S] =
        StateT.get[M, S]
    }

}

object ask extends AskInstances

