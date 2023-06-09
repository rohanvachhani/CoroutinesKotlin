package com.rohan.coroutineskotlin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rohan.coroutineskotlin.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * In Kotlin Coroutines, a dispatcher determines the thread for a coroutine to run on. Different dispatchers are available, each with their own uses and characteristics:

1. `Dispatchers.Default`: This dispatcher is backed by a shared pool of threads and is used for CPU-intensive tasks, such as computations and algorithms. It uses a number of threads equal to the number of CPU cores.

2. `Dispatchers.IO`: This dispatcher is designed for offloading blocking IO tasks to a shared pool of threads, such as file, socket read/writes, and database transactions. It's appropriate for tasks that involve waiting, such as network or disk operations.

3. `Dispatchers.Main`: This dispatcher is confined to the main (UI) thread in an application. It's primarily used for performing UI-related tasks in a coroutine, such as updating the UI in an Android application. Note that this dispatcher is not available in all environments, and its usage requires an implementation in a specific platform like Android or JavaFX.

4. `Dispatchers.Unconfined`: This dispatcher isn't confined to any specific thread. It executes the initial continuation of a coroutine in the current call-frame and lets the coroutine resume in whatever thread that is used by the corresponding suspending function, without mandating any specific threading policy. It should be used with caution because it can lead to behavior that's difficult to predict.

5. `newSingleThreadContext`: This isn't a dispatcher, but it's a function that creates a new thread and returns a `CoroutineDispatcher` that confines coroutines to this single thread. It can be useful for running test code, but in a production environment, it's recommended to use other dispatchers to avoid the cost of creating a new thread for each coroutine.

6. `newFixedThreadPoolContext`: Similar to `newSingleThreadContext`, this function creates a `CoroutineDispatcher` that uses a fixed number of threads. It creates a thread for each CPU core, but you can specify the number of threads if you prefer.

These dispatchers are used by passing them to coroutine builders such as `launch`, `async`, or `withContext`, for example:

```kotlin
GlobalScope.launch(Dispatchers.Main) {
// update UI
}
```

```kotlin
withContext(Dispatchers.IO) {
// perform IO operation
}
```

Remember to always choose an appropriate dispatcher for your coroutine. If you are doing CPU intensive work, choose `Dispatchers.Default`, for IO work use `Dispatchers.IO`, and for updating the UI use `Dispatchers.Main`.
 */

class MainActivity : AppCompatActivity() {
    val TAG = this.javaClass.canonicalName
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*  val executionTime = measureTimeMillis {
              GlobalScope.launch {
                  delay(1000L)
                  val resultNC1 = doNetworkCall1()        //it is sequential within the single coroutine (use 'async' for parallel)
                  Log.d(TAG, "result from network call 1 is:  $resultNC1")
                  val resultNC2 =
                      doNetworkCall2()        //it is sequential within the single coroutine (i.e  will not begin until doNetworkCall1() has completed)

                  Log.d(TAG, "result from network call 2 is:  $resultNC2")
              }
          }
          Log.d(TAG, "Time for execution is: $executionTime")*/

// ======================================================================

        /* GlobalScope.launch(Dispatchers.IO) {  // OR GlobalScope.launch(newSingleThread("MyThread"))

             Log.d(TAG, "current thread is: " + Thread.currentThread().name)       //"DefaultDispatcher-worker-<number>"
             val resultNC1 = doNetworkCall1()

             withContext(Dispatchers.Main) { //now the code inside this block will get executed in Main thread
                 Log.d(TAG, "result from network call 1 is:  $resultNC1")
                 Log.d(TAG, "current thread is: " + Thread.currentThread().name)      //"main"
                 //can show it in the textView, etc, etc..
             }
         }*/

// ======================================================================

        /**
         * 'runBlocking' : Start the coroutine will not BLOCK the current running thread(i.e main); so, "runBlocking" will block the main thread
         */

        /*  Log.d(TAG, "Before runBlocking")
          runBlocking {

              // Each "launch" creates a new coroutine and both are scheduled for execute concurrently, and unless specified otherwise, coroutines do not block each other.

              launch(Dispatchers.IO) {        //This is not block the main thread, only 'runBlocking' will do that
                  delay(3000L)
                  Log.d(TAG, "Finished IO coroutine 1")
              }

              launch(Dispatchers.IO) {        //both this launched coroutines will run parallel, so they both will complete in 3 sec.
                  delay(3000L)
                  Log.d(TAG, "Finished IO coroutine 2")
              }
              Log.d(TAG, "Start of RunBlocking")
              delay(3000L)
              Log.d(TAG, "End of RunBlocking")
          }*/

//======================================================================

        /**
         * Launch will return the job which can be stored into var
         */

        /*        val job = GlobalScope.launch(Dispatchers.Default) {
                    withTimeout(3000L) {
                        //can also check if this coroutine is still active with 'isActive()'
                        repeat(5) {
                            Log.d(TAG, "coroutine is still working...")
                            delay(1000L)               //introducing delay here, as without the delay, there won't be enough time to tell the coroutine that the this job is cancelled!
                        }
                    }

                }

                runBlocking {
                    job.join()          //it will block the main thread until the above coroutine finish
                    //can cancel a job with : cancel()    //but there has to be enough time to tell that coroutine that it has been cancelled!!
                    job.cancel()
                    Log.d(TAG, "Main thread is continuing...")
                }*/

//======================================================================

        /**
         * Running 2 suspend function parallely in one coroutine
         */

        /*  GlobalScope.launch(Dispatchers.IO) {        //GlobalScope.async(Dispatchers.IO) works too!
              val time = measureTimeMillis {

                  // we can do this to run parallelly but it is too much of a hassle, so instead we can use 'async' which return deferred(which we can access with await()
                  //                val job1 = launch {
                  //                    resultNC1 = doNetworkCall1()
                  //                }
                  //                val job2 = launch {
                  //                    resultNC1 = doNetworkCall2()
                  //                }
                  //                job1.join()
                  //                job2.join()

                  val resultNC1 = async { doNetworkCall1() }
                  val resultNC2 = async { doNetworkCall2() }
                  // val results = awaitAll(resultNC1, resultNC2)          //  wait for the completion of both tasks


                  Log.d(
                      TAG,
                      "result from network call 1 is:  ${resultNC1.await()}"
                  )  // await() : wait until resultNC1 is available
                  Log.d(
                      TAG,
                      "result from network call 2 is:  ${resultNC2.await()}"
                  )   //await() : wait until resultNC2 is available
              }
              Log.d(TAG, "Requests took $time ms")
          }*/

//======================================================================
        /**
         * Using coroutines in Lifecycle scope and viewModel scope (have to include dependencies for both in build.gradle file)
         *
         */

        /* binding.btnStartActivity.setOnClickListener {
             lifecycleScope.launch {         //GlobalScope.launch will make this coroutine to leak memory even when this activity is finished and we are in secondActivity. Using, the "LifecycleScope" will destroy running coroutines when that activity is finished
                 while (true) {
                     delay(1000L)
                     Log.d(TAG, "Still running...")
                 }
             }
             GlobalScope.launch {
                 delay(5000L)
                 Intent(this@MainActivity, SecondActivity::class.java).also {
                     startActivity(it)
                     finish()        //all coroutines which has been started in this main activity in 'LifecycleScope' will get destroyed
                 }

             }
 */

//======================================================================

        /**
         * Exception handling for "launch" and "async"
         */

//'launch' will immediately throw the exception while 'async' will throw the exception only when the result of 'async' will be accessed with 'await'

//Even though here, the exception is thrown inside 'async', it will be propagated to 'launch', and 'launch' will throw it immediately.

        /* lifecycleScope.launch {
             val string = async {
                 delay(500L)
                 throw Exception("Custom exception occurred in launch")
                 "Result"        //last st. is returned
             }
             println(string.await())
         }*/


//================================================
//exception in 'async'
        /* val deferred = lifecycleScope.async {
             val strOutput = async {
                 delay(500L)
                 throw Exception("Exception occurred in async")
                 "Result"
             }
         }

         lifecycleScope.launch {
             //Wrapping 'await()' in try catch..THIS IS NOT A RECOMMENDED WAY TO HANDLE EXCEPTIONS FROM ASYNC/LAUNCH
             try {
                 deferred.await()        //now at this time, 'async' will throw the exception
             } catch (e: Exception) {
                 Log.e(
                     TAG,
                     "Now at this time, async will throw the exception : ${e.localizedMessage}"
                 )
             }
         }*/

//=======================================================
        // RECOMMENDED WAY TO HANDLE EXCEPTIONS

      /*  val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e(TAG, "Caught exception: ${throwable.localizedMessage}")
        }

        //handling exception in launch
        lifecycleScope.launch(exceptionHandler) {
            val String = async {
                delay(500L)
                throw Exception("Exception occurred in 'launch'")
                "Result"        //last st. is returned
            }
        }

        val deferred = lifecycleScope.async {
            val strOutput = async {
                delay(500L)
                throw Exception("Exception occurred in 'async'")
                "Result"
            }
        }

        lifecycleScope.launch(exceptionHandler) {        //this coroutine is throwing exception, so need to include exceptionHandler here and not in the above coroutine where we are not actually using result of async(we're just getting the result via async)
            deferred.await()
        }*/

    }

    suspend fun doNetworkCall1(): String {
        delay(3000L)
        return "Response from network call 1"
    }

    suspend fun doNetworkCall2(): String {
        delay(3000L)
        return "Response from network call 2"
    }


}