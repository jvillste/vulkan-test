(ns vulkan-test.core
  (:import [org.lwjgl.vulkan VK11]
           [org.lwjgl.glfw GLFW GLFWVulkan Callbacks]
           [org.lwjgl.system MemoryUtil])
  (:require [vulkan-test.queue :as queue]
            [vulkan-test.cider-repl :as cider-repl]
            [nrepl.server :as server]
            [cider.nrepl.middleware :as middleware]
            [refactor-nrepl.middleware :as refactor-nrepl-middleware])
  (:gen-class))



(defn initialize-glfw []
  (GLFW/glfwInit))

(defn terminate-glfw []
  (GLFW/glfwTerminate))

(defn create-window [title width height]
  (GLFW/glfwWindowHint GLFW/GLFW_CLIENT_API GLFW/GLFW_NO_API)

  (GLFW/glfwCreateWindow width
                         height
                         title
                         MemoryUtil/NULL
                         MemoryUtil/NULL))

(defn poll-events []
  (GLFW/glfwPollEvents))

(defn destroy-window [window-handle]
  (Callbacks/glfwFreeCallbacks window-handle)
  (GLFW/glfwDestroyWindow window-handle))


;; command loop

(defn start-command-loop [should-terminate?-atom command-queue result-queue]
  (loop [function nil]
    (poll-events)

    (when function
      (println "running in thread " (.getId (Thread/currentThread))) ;; TODO: remove-me
      (queue/put result-queue (or (function)
                                  :nil)))

    (when (not @should-terminate?-atom)
      (recur (queue/take command-queue
                         500)))))

(defn start-main-command-loop []
  (def should-terminate?-atom (atom false))
  (def command-queue (queue/queue))
  (def result-queue (queue/queue))
  (start-command-loop should-terminate?-atom
                      command-queue
                      result-queue))

(defn terminate-main-command-loop []
  (reset! should-terminate?-atom true))

(defn run-in-main-thread [function]
  (queue/put command-queue function)
  (queue/take result-queue))

(defmacro in-main-thread [& body]
  `(run-in-main-thread (fn [] ~@body)))



(defn -main
  [& _args]
  (cider-repl/start-cider-nrepl)
  (initialize-glfw)

  ;; (def window-handle (create-window "Vulkan Test"
  ;;                                   100 100))

  ;; (while (not (GLFW/glfwWindowShouldClose window-handle))
  ;;   (poll-events))

  ;; (destroy-window window-handle)

  (start-main-command-loop)

  (terminate-glfw)
  (prn "glfw terminated")
  (System/exit 0)

  )



(comment
  (in-main-thread (def window-handle (create-window "Vulkan Test"
                                                    100 100)))
  (GLFW/glfwWindowShouldClose window-handle)
  (in-main-thread (destroy-window window-handle))
  (terminate-main-command-loop)

  )
