import React, { useContext, useEffect } from 'react';

import { matchPath, RouteComponentProps, withRouter } from 'react-router-dom';
import { Routes } from '../../../interfaces';
import componentStyles from '../../../Resources/styling/ComponentStyles';
import IconButton from '@material-ui/core/IconButton';
import { WarningRounded } from '@material-ui/icons';
import { LandscapeContext } from '../../../Context/LandscapeContext';
import StatusBar from './StatusBar';
import StatusBadge from '../Utils/StatusBadge';

interface Props extends RouteComponentProps {
  setSidebarContent: Function;
}

/**
 * Handles the landscape context on location changes.
 *
 *
 */
const LandscapeWatcher: React.FC<Props> = ({ setSidebarContent, ...props }) => {
  const context = useContext(LandscapeContext);
  const classes = componentStyles();

  //change identifier
  useEffect(() => {
    const match: { params?: { identifier?: string } } | null = matchPath(props.location.pathname, {
      path: Routes.MAP_ROUTE,
      exact: false,
      strict: false,
    });

    const pathIdentifier: string | null = match?.params?.identifier || null;

    if (pathIdentifier !== context.identifier) {
      context.next(pathIdentifier);
    }
  }, [props, context]);

  let color = 'white';
  if (context.landscape) {
    const assessmentSummary = context.getAssessmentSummary(
      context.landscape?.fullyQualifiedIdentifier
    );
    if (assessmentSummary) {
      color = assessmentSummary.status;
    }
  }

  if (context.identifier == null) return <></>;

  return (
    <StatusBadge
      overlap={'circle'}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'right',
      }}
      variant={'dot'}
      style={{ color: color }}
    >
      <IconButton
        size={'small'}
        className={classes.navigationButton}
        onClick={() => {
          if (context.landscape == null || context.assessment == null) {
            return;
          }
          setSidebarContent(
            <StatusBar
              landscape={context.landscape}
              setSidebarContent={setSidebarContent}
              assessments={context.assessment}
            />
          );
        }}
        title={'Show assessments (KPI-based warnings)'}
      >
        <WarningRounded />
      </IconButton>
    </StatusBadge>
  );
};

export default withRouter(LandscapeWatcher);
