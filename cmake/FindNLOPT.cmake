find_path(NLOPT_INCLUDE_DIR nlopt.h
  PATHS
  c:/nlopt/include
  ~/nlopt/include
)

find_library(NLOPT_LIBRARY
  NAMES libnlopt libnlopt-0 nlopt
  PATHS
  c:/nlopt/lib
  ~/nlopt/lib
)

set( NLOPT_INCLUDE_DIRS "${NLOPT_INCLUDE_DIR}")
set( NLOPT_LIBRARIES "${NLOPT_LIBRARY}")

