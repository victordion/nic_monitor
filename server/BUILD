scala_library(
  name='scala',
  dependencies=[
    ':3rdparty',
  ],
  sources=rglobs('*.scala')
)

jvm_binary(
  name='nic-monitor-server',
  basename='nic-monitor-server',
  main='info.jcui.nicmonitor.Server',
  dependencies=[
    ':scala',
  ]
)

jar_library(
  name='3rdparty',
  jars=[
    jar(org='com.twitter', name='finagle-http_2.11', rev='6.42.0', force=True),
    jar(org='com.twitter', name='util-core_2.11', rev='6.41.0', force=True),
    jar(org='com.twitter', name='twitter-server_2.11', rev='1.27.0', force=True),
    jar(org='org.json4s', name='json4s-native_2.11', rev='3.5.0', force=True),
    jar(org='mysql', name='mysql-connector-java', rev='5.1.12', force=True),
  ]
)

