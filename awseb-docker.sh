#!/bin/bash
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8

die () {
    echo >&2 "$@"
    exit 1
}

check_exit_code() {
  if [[ $1 -ne 0 ]] ; then
    echo "[`date`] ERROR OCCURRED... EXITING"
    exit $1
  fi
}

[ "$#" -ge 4 ] || die "<create or update> <app name> <env name> <coralogix_private_key(optional)> <coralogix_company_id(optional)> required, only $# arguments provided"
echo "[`date`] STARTING AWS EB configuration Docker..."

MODE=$1
APP_NAME=$2
ENV_NAME=$3
CORALOGIX_PRIVATE_KEY=$4
CORALOGIX_COMPANY_ID=$5

TIMESTAMP=`date +%s`

S3BUCKET=eb-${APP_NAME}-seoul
CNAME=''${ENV_NAME}'\\..*elasticbeanstalk.com'

recreate_configs() {
  echo "CNAME: ${CNAME} / APP_NAME: ${APP_NAME} / ENV_NAME: ${ENV_NAME}"
  ENV_ID=`aws --region ap-northeast-2 elasticbeanstalk describe-environments --application-name ${APP_NAME} | jq '.Environments[]| select(.CNAME|test("'${CNAME}'"))|select(.Status == "Ready")|.EnvironmentId' | sed -e 's/^"//' -e 's/"$//'`
  if [[ -n "$ENV_ID" ]]
  then
    echo "[`date`] Recreating configuration template $ENV_NAME"
    echo "delete old config"

    aws --region ap-northeast-2 elasticbeanstalk delete-configuration-template --application-name ${APP_NAME} --template-name ${ENV_NAME}
    check_exit_code $?
    echo "create new config"
    aws --region ap-northeast-2 elasticbeanstalk create-configuration-template --application-name ${APP_NAME} --template-name ${ENV_NAME} --environment-id ${ENV_ID}
    check_exit_code $?
  else
    echo "Could not find the target environment id. APP_NAME: ${APP_NAME}, ENV_ID: ${ENV_ID}"
    exit 1
  fi
}

mkdir -p fisherman/.ebextensions

# setup coralogix
if [[ -n "$CORALOGIX_PRIVATE_KEY" ]];then
  echo "[`date`] Setting up Coralogix config files..."
  sed "s/%{CORALOGIX_PRIVATE_KEY}%/$CORALOGIX_PRIVATE_KEY/g; s/%{APP_NAME}%/$APP_NAME/g; s/%{CORALOGIX_COMPANY_ID}%/$CORALOGIX_COMPANY_ID/g; s/%{ENV_NAME}%/$ENV_NAME/g;" .ebextensions/04-coralogix.config > fisherman/.ebextensions/04-coralogix.config
fi
check_exit_code $?

cp Dockerfile.awseb fisherman/Dockerfile
cp jks/key.jks build/libs/fisherman-pushserver.jar run.sh stop.sh fisherman

cd fisherman

GLOBIGNORE=.:..; zip -r $GIT_COMMIT.zip . -x ".git/*"; unset GLOBIGNORE

check_exit_code $?

echo "[`date`] Uploading Docker with GIT commit $GIT_COMMIT-$ENV_NAME for $APP_NAME..."
# -- AWS --
# Upload the image
aws --region ap-northeast-2 s3api put-object --bucket ${S3BUCKET} --key "$GIT_COMMIT-$ENV_NAME" --body $GIT_COMMIT.zip

check_exit_code $?

echo "[`date`] Creating new application version $TIMESTAMP-$ENV_NAME for $APP_NAME..."

# Create a new application version
aws --region ap-northeast-2 elasticbeanstalk create-application-version --application-name $APP_NAME --version-label "$TIMESTAMP-$ENV_NAME" --source-bundle S3Bucket=${S3BUCKET},S3Key="$GIT_COMMIT-$ENV_NAME"

check_exit_code $?

# Deploy the image
if [ $MODE = "update" ];then
  echo "[`date`] Updating env $ENV_NAME for $APP_NAME..."
  aws --region ap-northeast-2 elasticbeanstalk update-environment --environment-name $ENV_NAME --version-label "$TIMESTAMP-$ENV_NAME"
else
  if [ $MODE = "init" ];then
    echo "[`date`] Initializing env $ENV_NAME for $APP_NAME..."
    aws --region ap-northeast-2 elasticbeanstalk create-environment --application-name $APP_NAME --environment-name "$ENV_NAME" --cname-prefix "$ENV_NAME" --version-label "$TIMESTAMP-$ENV_NAME" --template-name $ENV_NAME
  else
    recreate_configs
    if [ $MODE = "create" ];then
      echo "[`date`] Creating env $ENV_NAME-$TIMESTAMP for $APP_NAME..."
      aws --region ap-northeast-2 elasticbeanstalk create-environment --application-name $APP_NAME --environment-name "$ENV_NAME-$TIMESTAMP" --version-label "$TIMESTAMP-$ENV_NAME" --template-name $ENV_NAME
    else
      die "Invalid deploy action: $MODE"
    fi
  fi
fi

check_exit_code $?

echo "DONE!"
