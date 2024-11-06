(ns minimal-clojure-project.manager
  (:require [clojure.core.async :as a :refer [<!! >!! <! >!]]
            [com.stuartsierra.component :as c]))

;; internal

(defn fetch-new-token! [_manager]
  {:value (str (rand-int 10000))
   :timeout (+ 1000 (* 1000 (rand-int 5)))})

(defn refresh-token! [manager]
  (let [token (fetch-new-token! manager)]
    (reset! (:token manager) token)
    token))

;; API

(defn get-token [manager]
  (-> manager :token deref))

(defn force-refresh! [manager]
  (>!! (:chan manager) "trigger alts!"))

(defn create-manager []
  (let [manager {:token (atom {:value ""
                               :timeout 1000})
                 :chan (a/chan)}]
    (a/go-loop [i 0]
      (let [[v ch] (a/alts! [(:chan manager)
                             (a/timeout (:timeout (get-token manager)))])
            token (refresh-token! manager)]
        (println {:i i :token token})
        (when-not (and (= (:chan manager) ch)
                       (nil? v))
          (recur (inc i)))))
    manager))

(defn stop-manager [manager]
  (a/close! (:chan manager)))

;; JWT component

(defrecord JWTManager []
  c/Lifecycle

  (start [component]
    (println "Starting JWT manager...")
    (let [manager (create-manager)]
      (assoc component :manager manager)))

  (stop [component]
    (println "Stopping JWT manager...")
    (stop-manager (:manager component))
    (assoc component :manager nil)))

(defn new-jwt-manager []
  (map->JWTManager {}))

(defn get-token [jwt-manager]
  (-> jwt-manager :token deref))

;; APP component

(defrecord AppComponent [jwt-manager]
  c/Lifecycle

  (start [component]
    (println "Starting AppComponent...")
    (assoc component :token (get-token jwt-manager))) ;; TODO This throws a NPE

  (stop [component]
    (println "Stopping AppComponent...")
    (assoc component :token nil)
    component))

(defn new-app-component []
  (map->AppComponent {}))

;; system

(defn example-system []
  (c/system-map
    :jwt-manager (new-jwt-manager)
    :app (c/using (new-app-component) [:jwt-manager])))


(comment
  ; requirements:
  ; action on timeout
  ; externally readable value
  ; forced action
  ;
  ; API
  ; start or create thingy

  (def system (example-system))

  (alter-var-root #'system c/start)

  (alter-var-root #'system c/stop)

  ;
  ;(new-jwt-manager)
  ;
  ;(def manager (create-manager))
  ;
  ;(stop-manager manager)
  ;
  ;(force-refresh! manager)
  ;
  ;(get-token manager)
  ;
  ;(refresh-token! manager)
  ;
  ;;; timeout
  ;
  ;;; if unbuffered channel
  ;
  ;(def my-chan (a/chan 1)) ;; stays open
  ;
  ;;; go blocks creates "new thread" lightweight non OS threads
  ;(a/go
  ;  (println "received:" (<! (a/timeout 1000))))
  ;
  ;(a/go
  ;  (println "received:" (<! my-chan)))
  ;
  ;(a/go
  ;  (while true
  ;    (println "loop received:" (<! my-chan)))) ;; prints to STDOUT (on different thread but can see it in main thread)
  ;
  ;;; runs in main thread of REPL
  ;(>!! my-chan "Loopsie4") ;; blocks a real OS thread
  ;
  ;(a/go
  ;  (while true
  ;    (let [[v ch] (a/alts! [my-chan (a/timeout 5000)])] ;; whichever returns v first
  ;      (println [v ch]))))
  ;
  ;
  ;(>!! my-chan "Loopsie1")
  ;
  ;(a/go-loop [i 0]
  ;  (let [[_v _ch] (a/alts! [(:chan manager)
  ;                           (a/timeout (:timeout (get-token manager)))])
  ;        token (refresh-token! manager)]
  ;    (println {:i i :token token})
  ;    (recur (inc i))))




  42)