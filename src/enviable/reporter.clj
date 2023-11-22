(ns enviable.reporter)

(defprotocol ConfigReporter
  (report-config-status [config-error])
  (document-config [config]))
