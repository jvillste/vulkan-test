(ns vulkan-test.cider-repl
  (:require [vulkan-test.queue :as queue]
            [nrepl.server :as server]
            [cider.nrepl.middleware :as middleware]
            [refactor-nrepl.middleware :as refactor-nrepl-middleware]))

;; from https://github.com/clojure-emacs/refactor-nrepl
(def custom-nrepl-handler
  "We build our own custom nrepl handler, mimicking CIDER's."
  (apply server/default-handler
         (conj cider.nrepl.middleware/cider-middleware
               'refactor-nrepl.middleware/wrap-refactor)))

(defn start-cider-nrepl
  []
  (println "Starting Cider Nrepl Server Port 7888")
  (defonce nrepl-server (server/start-server :port 7888
                                             :handler custom-nrepl-handler #_nrepl/cider-nrepl-handler))
  (println "Nrepl Serever started")
  #_(.start (Thread. (fn []
                       (println "Starting Cider Nrepl Server Port 7888")
                       (server/start-server :port 7888 :handler cider/cider-nrepl-handler)))))
