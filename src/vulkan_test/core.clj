(ns vulkan-test.core
  (:import [org.lwjgl.vulkan VK11]
           [org.lwjgl.glfw GLFW GLFWVulkan Callbacks]
           [org.lwjgl.system MemoryUtil])
  (:require [vulkan-test.queue :as queue]
            [vulkan-test.cider-repl :as cider-repl]
            [vulkan-test.main-thread :as main-thread])
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

(defn -main
  [& _args]
  (cider-repl/start-cider-nrepl)
  (initialize-glfw)

  (main-thread/enter-main-thread-command-loop 500
                                              poll-events)

  (terminate-glfw)
  (System/exit 0))


(comment
  (main-thread/in-main-thread (def window-handle (create-window "Vulkan Test"
                                                                100 100)))
  (main-thread/in-main-thread (GLFW/glfwWindowShouldClose window-handle))
  (main-thread/in-main-thread (destroy-window window-handle))
  (main-thread/terminate-main-thread-command-loop)

  )
