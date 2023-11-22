(ns enviable.reporter)

(defprotocol ConfigReporter
  (report-config-status [this config-error])
  (document-config [this config]))
