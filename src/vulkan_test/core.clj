(ns vulkan-test.core
  (:import ;; [org.lwjgl.vulkan]
           [org.lwjgl.glfw GLFW Callbacks GLFWFramebufferSizeCallback GLFWKeyCallback GLFWCursorPosCallback GLFWCursorEnterCallback GLFWMouseButtonCallback]
           [org.lwjgl.system MemoryUtil])
  (:require [vulkan-test.cider-repl :as cider-repl]
            [vulkan-test.main-thread :as main-thread])
  (:gen-class))

(defn initialize-glfw []
  (GLFW/glfwInit))

(defn terminate-glfw []
  (GLFW/glfwTerminate))

(defn create-window [title width height event-callback]
  (GLFW/glfwWindowHint GLFW/GLFW_CLIENT_API GLFW/GLFW_NO_API)

  (let [window-handle (GLFW/glfwCreateWindow width
                                             height
                                             title
                                             MemoryUtil/NULL
                                             MemoryUtil/NULL)]

    (GLFW/glfwSetFramebufferSizeCallback window-handle
                                         (proxy [GLFWFramebufferSizeCallback] []
                                           (invoke [_window-handle width height]
                                             (event-callback {:type :framebuffer-resized
                                                              :width width
                                                              :height height}))))

    (GLFW/glfwSetKeyCallback window-handle
                             (proxy [GLFWKeyCallback] []
                               (invoke [_window-handle key scancode action modifiers]
                                 (event-callback {:type (if (= GLFW/GLFW_PRESS action)
                                                          :key-pressed
                                                          :key-released)
                                                  :scancode scancode
                                                  :modifiers modifiers}))))

    (GLFW/glfwSetCursorPosCallback window-handle
                                   (proxy [GLFWCursorPosCallback] []
                                     (invoke [_window-handle x y]
                                       (event-callback {:type :mouse-moved
                                                        :x x
                                                        :y y}))))

    (GLFW/glfwSetCursorEnterCallback window-handle
                                     (proxy [GLFWCursorEnterCallback] []
                                       (invoke [_window-handle entered?]
                                         (event-callback {:type (if entered?
                                                                  :mouse-entered
                                                                  :mouse-left)}))))

    (GLFW/glfwSetMouseButtonCallback window-handle
                                     (proxy [GLFWMouseButtonCallback] []
                                       (invoke [_window-handle button action modifiers]
                                         (event-callback {:type (if (= GLFW/GLFW_PRESS action)
                                                                  :mouse-button-pressed
                                                                  :mouse-button-released)
                                                          :button button
                                                          :modifiers modifiers}))))
    window-handle))

(defn poll-events []
  (GLFW/glfwPollEvents))

(defn destroy-window [window-handle]
  (Callbacks/glfwFreeCallbacks window-handle)
  (GLFW/glfwDestroyWindow window-handle))

(defn -main [& _args]

  (cider-repl/start-cider-nrepl)
  (initialize-glfw)

  (main-thread/start-command-loop 500 poll-events)

  (terminate-glfw)
  (System/exit 0))


(comment

  (main-thread/in-main-thread (def window-handle (create-window "Vulkan Test"
                                                                100
                                                                100
                                                                (fn [event]
                                                                  (prn event)))))

  (main-thread/in-main-thread (GLFW/glfwWindowShouldClose window-handle))
  (main-thread/in-main-thread (destroy-window window-handle))
  (main-thread/terminate-command-loop)

  )
