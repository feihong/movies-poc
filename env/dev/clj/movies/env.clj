(ns movies.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [movies.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[movies started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[movies has shut down successfully]=-"))
   :middleware wrap-dev})
