(ns eventure.app
  (:require [eventure.domain :as domain])
  (:import (java.util UUID)))

(defn create-event [save-event cmd]
  (let [id (UUID/randomUUID)
        details (:event-details cmd)
        items (:event-items cmd)
        event (domain/create-event id details items)]
    (save-event event)
    event))


