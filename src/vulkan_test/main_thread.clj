(ns vulkan-test.main-thread
  (:require [vulkan-test.queue :as queue]))

(def should-terminate?-atom (atom false))
(def command-queue (queue/queue))
(def result-queue (queue/queue))

(defn start-command-loop [poll-timeout run-on-every-poll]
  (loop [function nil]
    (run-on-every-poll)

    (when function
      (println "running in thread " (.getId (Thread/currentThread))) ;; TODO: remove-me
      (queue/put result-queue (let [result (function)]
                                (if (nil? result)
                                  :nil
                                  result))))

    (when (not @should-terminate?-atom)
      (recur (queue/take command-queue
                         poll-timeout)))))

(defn terminate-command-loop []
  (reset! should-terminate?-atom true))

(defn run-in-main-thread [function]
  (queue/put command-queue function)
  (queue/take result-queue))

(defmacro in-main-thread [& body]
  `(run-in-main-thread (fn [] ~@body)))
