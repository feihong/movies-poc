(ns movies.routes.services
  (:require [ring.util.http-response :refer [ok]]
            [compojure.api.sweet :refer [defapi context GET POST]]
            [schema.core :as s]
            [movies.showings :refer [movie-showings]]))


(def Movie
  {:title s/Str
   :url (s/maybe s/Str)
   :matches-criteria s/Bool
   :director s/Str
   :actors s/Str
   :year s/Int
   :runtime (s/maybe s/Str)
   (s/optional-key :poster) s/Str
   (s/optional-key :country) s/Str
   (s/optional-key :language) s/Str
   :plot s/Str})


(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Sample API"
                           :description "Sample Services"}}}}

  (context "/api" []
    :tags ["movies"]

    (GET "/plus" []
      :return       Long
      :query-params [x :- Long, {y :- Long 1}]
      :summary      "x+y with query-parameters. y defaults to 1."
      (ok (+ x y)))

    (GET "/showings" []
      :return       [Movie]
      :summary      "Show movies playing now"
      (ok (movie-showings)))))
