(ns movies.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[movies started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[movies has shut down successfully]=-"))
   :middleware identity})
